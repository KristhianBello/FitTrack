package com.example.fittrack

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.example.fittrack.shared.FitTrackSdk
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    // UI Components
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            android.util.Log.d("MainActivity", "Iniciando onCreate")
            setContentView(R.layout.activity_main)
            android.util.Log.d("MainActivity", "Layout establecido correctamente")

            // Inicializar vistas
            initializeViews()
            android.util.Log.d("MainActivity", "Vistas inicializadas correctamente")

            // Configurar toolbar
            setupToolbar()
            android.util.Log.d("MainActivity", "Toolbar configurado correctamente")

            // Configurar navegación
            setupNavigation()
            android.util.Log.d("MainActivity", "Navegación configurada correctamente")

            // Cargar fragment inicial
            if (savedInstanceState == null) {
                loadInitialFragment()
                android.util.Log.d("MainActivity", "Fragment inicial cargado correctamente")
            }

            restoreSessionIfNeeded()

            android.util.Log.d("MainActivity", "onCreate completado exitosamente")
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Error crítico en onCreate: ${e.message}", e)
            // Re-lanzar para que se vea en el crash log
            throw e
        }
    }

    private fun initializeViews() {
        try {
            android.util.Log.d("MainActivity", "Buscando bottomNavigationView...")
            bottomNavigationView = findViewById(R.id.bottomNavigationView)
            android.util.Log.d("MainActivity", "bottomNavigationView encontrado")


            android.util.Log.d("MainActivity", "Buscando drawerLayout...")
            drawerLayout = findViewById(R.id.drawer_layout)
            android.util.Log.d("MainActivity", "drawerLayout encontrado")

            android.util.Log.d("MainActivity", "Buscando navigationView...")
            navigationView = findViewById(R.id.nav_view)
            android.util.Log.d("MainActivity", "navigationView encontrado")

            android.util.Log.d("MainActivity", "Buscando toolbar...")
            toolbar = findViewById(R.id.toolbar)
            android.util.Log.d("MainActivity", "toolbar encontrado")

            android.util.Log.d("MainActivity", "Todas las vistas inicializadas correctamente")
        } catch (e: Exception) {
            // Log del error si alguna vista no se encuentra
            android.util.Log.e("MainActivity", "Error inicializando vistas: ${e.message}", e)
            throw e // Re-lanzar para que se vea en el crash
        }
    }

    private fun setupToolbar() {
        try {
            // Configurar el Toolbar correctamente sin conflictos
            setSupportActionBar(toolbar)

            val toggle = ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            )
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            // Configurar el título de la ActionBar
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.title = "FitTrack"

            android.util.Log.d("MainActivity", "Toolbar y DrawerToggle configurados correctamente")
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Error configurando toolbar: ${e.message}", e)
            throw e
        }
    }

    private fun restoreSessionIfNeeded() {
        lifecycleScope.launch {
            if (FitTrackSdk.auth.isUserLoggedIn() && FitTrackSdk.session.profile.value == null) {
                FitTrackSdk.onAuthenticated()
            }
        }
    }

    private fun loadInitialFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, HomeFragment2())
            .commit()
        bottomNavigationView.selectedItemId = R.id.nav_home
    }

    private fun setupNavigation() {
        try {
            // Configurar navegación del bottom navigation
            bottomNavigationView.setOnItemSelectedListener { menuItem ->
                when(menuItem.itemId) {
                    R.id.nav_home -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.frame_layout, HomeFragment2())
                            .commit()
                        true
                    }
                    R.id.nav_exercise -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.frame_layout, RutinaFragment())
                            .commit()
                        true
                    }
                    R.id.nav_weight -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.frame_layout, DetalleFragment())
                            .commit()
                        true
                    }
                    R.id.nav_profile -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.frame_layout, ProfileFragment())
                            .commit()
                        true
                    }
                    else -> false
                }
            }

            // Configurar navegación del drawer
            navigationView.setNavigationItemSelectedListener { menuItem ->
                try {
                    when(menuItem.itemId) {
                        R.id.nav_home -> {
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.frame_layout, HomeFragment2())
                                .commit()
                            bottomNavigationView.selectedItemId = R.id.nav_home
                        }
                        R.id.nav_settings -> {
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.frame_layout, ProfileFragment())
                                .commit()
                            bottomNavigationView.selectedItemId = R.id.nav_profile
                        }
                        R.id.nav_share -> {
                            // Implementar funcionalidad de compartir
                            android.widget.Toast.makeText(this, "Funcionalidad de compartir próximamente", android.widget.Toast.LENGTH_SHORT).show()
                        }
                        R.id.nav_about -> {
                            // Implementar pantalla de About Us
                            android.widget.Toast.makeText(this, "Acerca de FitTrack v1.0", android.widget.Toast.LENGTH_SHORT).show()
                        }
                        R.id.nav_calendario -> {
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.frame_layout, CalendarioFragment())
                                .commit()
                        }
                        R.id.nav_logout -> {
                            // Cerrar sesión y redirigir a LoginActivity
                            performLogout()
                        }
                    }
                    drawerLayout.closeDrawers()
                    true
                } catch (e: Exception) {
                    android.util.Log.e("MainActivity", "Error en drawer navigation: ${e.message}", e)
                    drawerLayout.closeDrawers()
                    false
                }
            }

            // Configurar manejo del botón atrás
            onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (drawerLayout.isDrawerOpen(androidx.core.view.GravityCompat.START)) {
                        drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
                    } else {
                        finish()
                    }
                }
            })

            android.util.Log.d("MainActivity", "Navegación configurada correctamente")
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Error configurando navegación: ${e.message}", e)
            throw e
        }
    }

    private fun performLogout() {
        android.widget.Toast.makeText(this, "Cerrando sesión...", android.widget.Toast.LENGTH_SHORT).show()

        lifecycleScope.launch {
            try {
                // Cerrar sesión en Supabase
                FitTrackSdk.signOut()

                // Redirigir a LoginActivity
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()

                android.widget.Toast.makeText(
                    this@MainActivity,
                    "Sesión cerrada exitosamente",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "Error al cerrar sesión: ${e.message}", e)
                android.widget.Toast.makeText(
                    this@MainActivity,
                    "Error al cerrar sesión",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
