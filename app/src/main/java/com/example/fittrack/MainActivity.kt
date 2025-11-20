package com.example.fittrack

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

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
    }

    private fun loadInitialFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, HomeFragment())
            .commit()
        bottomNavigationView.selectedItemId = R.id.nav_home
    }

    private fun setupNavigation() {
        // Configurar navegación del bottom navigation
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.nav_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, HomeFragment())
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
            when(menuItem.itemId) {
                R.id.nav_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, HomeFragment())
                        .commit()
                    bottomNavigationView.selectedItemId = R.id.nav_home
                }
                R.id.nav_settings -> {
                    // Navegar a configuración
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, ProfileFragment())
                        .commit()
                }
                R.id.nav_share -> {
                    // Implementar funcionalidad de compartir
                }
                R.id.nav_about -> {
                    // Implementar pantalla de About Us
                }
                R.id.nav_logout -> {
                    // Implementar funcionalidad de logout
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
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
    }
}
