-- FitTrack database schema
-- Supabase/PostgreSQL

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE public.profiles (
  id uuid NOT NULL,
  name text NOT NULL,
  email text NOT NULL UNIQUE,
  avatar_url text,
  peso_meta numeric,
  altura numeric,
  fecha_nacimiento date,
  genero text CHECK (genero = ANY (ARRAY['masculino'::text, 'femenino'::text, 'otro'::text, 'prefiero_no_decir'::text])),
  created_at timestamp with time zone DEFAULT timezone('utc'::text, now()),
  updated_at timestamp with time zone DEFAULT timezone('utc'::text, now()),
  CONSTRAINT profiles_pkey PRIMARY KEY (id),
  CONSTRAINT profiles_id_fkey FOREIGN KEY (id) REFERENCES auth.users(id)
);

CREATE TABLE public.exercises (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  nombre text NOT NULL,
  descripcion text,
  grupo_muscular text,
  nivel text CHECK (nivel = ANY (ARRAY['principiante'::text, 'intermedio'::text, 'avanzado'::text])),
  tipo text CHECK (tipo = ANY (ARRAY['fuerza'::text, 'cardio'::text, 'flexibilidad'::text, 'otro'::text])),
  instrucciones text,
  video_url text,
  created_at timestamp with time zone DEFAULT timezone('utc'::text, now()),
  CONSTRAINT exercises_pkey PRIMARY KEY (id)
);

CREATE TABLE public.routines (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  user_id uuid NOT NULL,
  nombre text NOT NULL,
  duracion integer NOT NULL,
  cantidad_ejercicios integer NOT NULL,
  frecuencia_semanal integer NOT NULL,
  descripcion text,
  is_active boolean DEFAULT true,
  created_at timestamp with time zone DEFAULT timezone('utc'::text, now()),
  updated_at timestamp with time zone DEFAULT timezone('utc'::text, now()),
  CONSTRAINT routines_pkey PRIMARY KEY (id),
  CONSTRAINT routines_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.profiles(id)
);

CREATE TABLE public.routine_exercises (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  routine_id uuid NOT NULL,
  exercise_id uuid NOT NULL,
  orden integer NOT NULL,
  series integer DEFAULT 3,
  repeticiones integer DEFAULT 10,
  peso numeric,
  tiempo_descanso integer,
  notas text,
  CONSTRAINT routine_exercises_pkey PRIMARY KEY (id),
  CONSTRAINT routine_exercises_routine_id_fkey FOREIGN KEY (routine_id) REFERENCES public.routines(id),
  CONSTRAINT routine_exercises_exercise_id_fkey FOREIGN KEY (exercise_id) REFERENCES public.exercises(id)
);

CREATE TABLE public.sensor_data (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  user_id uuid NOT NULL,
  tipo_sensor text NOT NULL,
  valor numeric NOT NULL,
  unidad text,
  fecha timestamp with time zone DEFAULT timezone('utc'::text, now()),
  metadata jsonb,
  CONSTRAINT sensor_data_pkey PRIMARY KEY (id),
  CONSTRAINT sensor_data_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.profiles(id)
);

CREATE TABLE public.weight_records (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  user_id uuid NOT NULL,
  peso numeric NOT NULL,
  fecha date NOT NULL DEFAULT CURRENT_DATE,
  notas text,
  created_at timestamp with time zone DEFAULT timezone('utc'::text, now()),
  CONSTRAINT weight_records_pkey PRIMARY KEY (id),
  CONSTRAINT weight_records_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.profiles(id)
);

CREATE TABLE public.workout_history (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  user_id uuid NOT NULL,
  routine_id uuid,
  fecha timestamp with time zone DEFAULT timezone('utc'::text, now()),
  duracion_real integer,
  calorias_quemadas integer,
  notas text,
  completed boolean DEFAULT true,
  created_at timestamp with time zone DEFAULT timezone('utc'::text, now()),
  CONSTRAINT workout_history_pkey PRIMARY KEY (id),
  CONSTRAINT workout_history_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.profiles(id),
  CONSTRAINT workout_history_routine_id_fkey FOREIGN KEY (routine_id) REFERENCES public.routines(id)
);
