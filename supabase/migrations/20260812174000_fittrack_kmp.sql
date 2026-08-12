-- FitTrack KMP — esquema inicial
-- Fuente de verdad para las tablas que consume :shared (PostgREST).
-- Pensado para un proyecto vacío. No aplicar sobre tablas existentes sin backup.

create extension if not exists pgcrypto;

create schema if not exists private;
revoke all on schema private from public;
revoke all on schema private from anon, authenticated;

-- ---------------------------------------------------------------------------
-- Funciones internas (security definer fuera de public)
-- ---------------------------------------------------------------------------

create or replace function private.set_updated_at()
returns trigger
language plpgsql
set search_path = ''
as $$
begin
  new.updated_at = timezone('utc', now());
  return new;
end;
$$;

create or replace function private.handle_new_user()
returns trigger
language plpgsql
security definer
set search_path = ''
as $$
begin
  insert into public.profiles (id, name, email)
  values (
    new.id,
    coalesce(
      new.raw_user_meta_data ->> 'name',
      nullif(split_part(coalesce(new.email, ''), '@', 1), ''),
      'Atleta'
    ),
    coalesce(new.email, '')
  )
  on conflict (id) do nothing;
  return new;
end;
$$;

revoke all on function private.set_updated_at() from public, anon, authenticated;
revoke all on function private.handle_new_user() from public, anon, authenticated;

-- ---------------------------------------------------------------------------
-- Tablas
-- ---------------------------------------------------------------------------

create table public.profiles (
  id uuid primary key references auth.users (id) on delete cascade,
  name text not null,
  email text not null unique,
  avatar_url text,
  peso_meta numeric(5, 2),
  altura numeric(5, 2),
  fecha_nacimiento date,
  genero text check (
    genero = any (array['masculino', 'femenino', 'otro', 'prefiero_no_decir'])
  ),
  created_at timestamptz not null default timezone('utc', now()),
  updated_at timestamptz not null default timezone('utc', now())
);

create table public.exercises (
  id uuid primary key default gen_random_uuid(),
  nombre text not null,
  descripcion text,
  grupo_muscular text,
  nivel text check (nivel = any (array['principiante', 'intermedio', 'avanzado'])),
  tipo text check (tipo = any (array['fuerza', 'cardio', 'flexibilidad', 'otro'])),
  instrucciones text,
  video_url text,
  created_at timestamptz not null default timezone('utc', now())
);

create table public.routines (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.profiles (id) on delete cascade,
  nombre text not null,
  duracion integer not null check (duracion between 1 and 300),
  cantidad_ejercicios integer not null check (cantidad_ejercicios between 1 and 50),
  frecuencia_semanal integer not null check (frecuencia_semanal between 1 and 7),
  descripcion text,
  is_active boolean not null default true,
  created_at timestamptz not null default timezone('utc', now()),
  updated_at timestamptz not null default timezone('utc', now())
);

create table public.routine_exercises (
  id uuid primary key default gen_random_uuid(),
  routine_id uuid not null references public.routines (id) on delete cascade,
  exercise_id uuid not null references public.exercises (id) on delete restrict,
  orden integer not null check (orden >= 1),
  series integer not null default 3 check (series between 1 and 50),
  repeticiones integer not null default 10 check (repeticiones between 1 and 500),
  peso numeric(6, 2),
  tiempo_descanso integer,
  notas text,
  unique (routine_id, orden)
);

create table public.sensor_data (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.profiles (id) on delete cascade,
  tipo_sensor text not null check (
    tipo_sensor = any (array['proximidad', 'luz', 'acelerometro', 'shake'])
  ),
  valor numeric(12, 4) not null,
  unidad text,
  fecha timestamptz not null default timezone('utc', now()),
  metadata jsonb
);

create table public.weight_records (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.profiles (id) on delete cascade,
  peso numeric(5, 2) not null check (peso > 0 and peso <= 300),
  fecha date not null default current_date,
  notas text,
  created_at timestamptz not null default timezone('utc', now())
);

create table public.workout_history (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.profiles (id) on delete cascade,
  routine_id uuid references public.routines (id) on delete set null,
  fecha timestamptz not null default timezone('utc', now()),
  duracion_real integer check (duracion_real is null or duracion_real between 1 and 600),
  calorias_quemadas integer check (calorias_quemadas is null or calorias_quemadas >= 0),
  notas text,
  completed boolean not null default true,
  created_at timestamptz not null default timezone('utc', now())
);

-- ---------------------------------------------------------------------------
-- Índices (FK + filtros de RLS y listados de la app)
-- ---------------------------------------------------------------------------

create index profiles_email_idx on public.profiles (email);
create index routines_user_id_created_at_idx on public.routines (user_id, created_at desc);
create index routine_exercises_routine_id_orden_idx on public.routine_exercises (routine_id, orden);
create index routine_exercises_exercise_id_idx on public.routine_exercises (exercise_id);
create index sensor_data_user_id_fecha_idx on public.sensor_data (user_id, fecha desc);
create index weight_records_user_id_fecha_idx on public.weight_records (user_id, fecha desc);
create index workout_history_user_id_fecha_idx on public.workout_history (user_id, fecha desc);
create index workout_history_routine_id_idx on public.workout_history (routine_id);

-- ---------------------------------------------------------------------------
-- Triggers
-- ---------------------------------------------------------------------------

create trigger profiles_set_updated_at
  before update on public.profiles
  for each row execute function private.set_updated_at();

create trigger routines_set_updated_at
  before update on public.routines
  for each row execute function private.set_updated_at();

create trigger on_auth_user_created
  after insert on auth.users
  for each row execute function private.handle_new_user();

-- ---------------------------------------------------------------------------
-- RLS
-- auth.uid() envuelto en SELECT para no evaluarlo por fila.
-- UPDATE necesita también política SELECT.
-- ---------------------------------------------------------------------------

alter table public.profiles enable row level security;
alter table public.exercises enable row level security;
alter table public.routines enable row level security;
alter table public.routine_exercises enable row level security;
alter table public.sensor_data enable row level security;
alter table public.weight_records enable row level security;
alter table public.workout_history enable row level security;

create policy profiles_select_own on public.profiles
  for select to authenticated
  using (id = (select auth.uid()));

create policy profiles_insert_own on public.profiles
  for insert to authenticated
  with check (id = (select auth.uid()));

create policy profiles_update_own on public.profiles
  for update to authenticated
  using (id = (select auth.uid()))
  with check (id = (select auth.uid()));

create policy exercises_select_authenticated on public.exercises
  for select to authenticated
  using (true);

create policy routines_select_own on public.routines
  for select to authenticated
  using (user_id = (select auth.uid()));

create policy routines_insert_own on public.routines
  for insert to authenticated
  with check (user_id = (select auth.uid()));

create policy routines_update_own on public.routines
  for update to authenticated
  using (user_id = (select auth.uid()))
  with check (user_id = (select auth.uid()));

create policy routines_delete_own on public.routines
  for delete to authenticated
  using (user_id = (select auth.uid()));

create policy routine_exercises_select_own on public.routine_exercises
  for select to authenticated
  using (
    exists (
      select 1 from public.routines r
      where r.id = routine_id and r.user_id = (select auth.uid())
    )
  );

create policy routine_exercises_insert_own on public.routine_exercises
  for insert to authenticated
  with check (
    exists (
      select 1 from public.routines r
      where r.id = routine_id and r.user_id = (select auth.uid())
    )
  );

create policy routine_exercises_update_own on public.routine_exercises
  for update to authenticated
  using (
    exists (
      select 1 from public.routines r
      where r.id = routine_id and r.user_id = (select auth.uid())
    )
  )
  with check (
    exists (
      select 1 from public.routines r
      where r.id = routine_id and r.user_id = (select auth.uid())
    )
  );

create policy routine_exercises_delete_own on public.routine_exercises
  for delete to authenticated
  using (
    exists (
      select 1 from public.routines r
      where r.id = routine_id and r.user_id = (select auth.uid())
    )
  );

create policy sensor_data_select_own on public.sensor_data
  for select to authenticated
  using (user_id = (select auth.uid()));

create policy sensor_data_insert_own on public.sensor_data
  for insert to authenticated
  with check (user_id = (select auth.uid()));

create policy sensor_data_delete_own on public.sensor_data
  for delete to authenticated
  using (user_id = (select auth.uid()));

create policy weight_records_select_own on public.weight_records
  for select to authenticated
  using (user_id = (select auth.uid()));

create policy weight_records_insert_own on public.weight_records
  for insert to authenticated
  with check (user_id = (select auth.uid()));

create policy weight_records_update_own on public.weight_records
  for update to authenticated
  using (user_id = (select auth.uid()))
  with check (user_id = (select auth.uid()));

create policy weight_records_delete_own on public.weight_records
  for delete to authenticated
  using (user_id = (select auth.uid()));

create policy workout_history_select_own on public.workout_history
  for select to authenticated
  using (user_id = (select auth.uid()));

create policy workout_history_insert_own on public.workout_history
  for insert to authenticated
  with check (user_id = (select auth.uid()));

create policy workout_history_update_own on public.workout_history
  for update to authenticated
  using (user_id = (select auth.uid()))
  with check (user_id = (select auth.uid()));

create policy workout_history_delete_own on public.workout_history
  for delete to authenticated
  using (user_id = (select auth.uid()));
