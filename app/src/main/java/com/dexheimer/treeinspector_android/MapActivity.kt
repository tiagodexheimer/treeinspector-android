package com.dexheimer.treeinspector_android

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

	private lateinit var mMap: GoogleMap
	private lateinit var fusedLocationClient: FusedLocationProviderClient
	private var routePoints = mutableListOf<Pair<LatLng, String>>()
	private var markers = mutableListOf<Marker>()
	private var currentPointIndex = 0

	private lateinit var buttonNavigate: Button
	private lateinit var buttonMarkComplete: Button
	private lateinit var textViewNextDestination: TextView

	private val locationPermissionRequest = registerForActivityResult(
		ActivityResultContracts.RequestMultiplePermissions()
	) { permissions ->
		when {
			permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> showUserLocation()
			permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> showUserLocation()
			else -> Toast.makeText(this, "Permissão de localização negada.", Toast.LENGTH_SHORT).show()
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_map) // Você precisará criar este layout

		if (savedInstanceState != null) {
			currentPointIndex = savedInstanceState.getInt("CURRENT_INDEX", 0)
		}

		val addressesAsStrings = intent.getStringArrayListExtra("ROUTE_ADDRESSES") ?: emptyList()
		addressesAsStrings.forEach {
			val parts = it.split(",")
			val lat = parts[0].toDoubleOrNull()
			val lng = parts[1].toDoubleOrNull()
			val title = parts.subList(2, parts.size).joinToString(",")
			if (lat != null && lng != null) {
				routePoints.add(Pair(LatLng(lat, lng), title))
			}
		}

		fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

		buttonNavigate = findViewById(R.id.buttonNavigate)
		buttonMarkComplete = findViewById(R.id.buttonMarkComplete)
		textViewNextDestination = findViewById(R.id.textViewNextDestination)

		val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
		mapFragment.getMapAsync(this)

		buttonNavigate.setOnClickListener { navigateToNextPoint() }
		buttonMarkComplete.setOnClickListener { markPointAsComplete() }
	}

	override fun onMapReady(googleMap: GoogleMap) {
		mMap = googleMap
		checkLocationPermission()
		plotAllMarkers()
		updateUiForCurrentPoint()
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		outState.putInt("CURRENT_INDEX", currentPointIndex)
	}

	private fun markPointAsComplete() {
		if (currentPointIndex >= routePoints.size) return
		currentPointIndex++
		updateUiForCurrentPoint()
	}

	private fun updateUiForCurrentPoint() {
		markers.forEachIndexed { index, marker ->
			when {
				index < currentPointIndex -> marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
				index == currentPointIndex -> marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
				else -> marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
			}
		}

		if (currentPointIndex < routePoints.size) {
			val nextPoint = routePoints[currentPointIndex]
			textViewNextDestination.text = nextPoint.second
			buttonNavigate.isEnabled = true
			buttonMarkComplete.isEnabled = true
			buttonMarkComplete.text = "Ponto ${currentPointIndex + 1} Concluído"
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(nextPoint.first, 15f))
		} else {
			textViewNextDestination.text = "Rota Concluída!"
			buttonNavigate.isEnabled = false
			buttonMarkComplete.isEnabled = false
			buttonMarkComplete.text = "Finalizado"
			Toast.makeText(this, "Parabéns, você completou a rota!", Toast.LENGTH_LONG).show()
			Handler(Looper.getMainLooper()).postDelayed({
				val intent = Intent(this, DashboardActivity::class.java)
				intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
				startActivity(intent)
				finish()
			}, 2000)
		}
	}

	private fun plotAllMarkers() {
		if (routePoints.isEmpty()) return
		val boundsBuilder = LatLngBounds.builder()
		markers.clear()
		routePoints.forEach { point ->
			val marker = mMap.addMarker(MarkerOptions().position(point.first).title(point.second))
			if (marker != null) {
				markers.add(marker)
			}
			boundsBuilder.include(point.first)
		}
		if (currentPointIndex == 0) {
			mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100))
		}
	}

	private fun navigateToNextPoint() {
		if (currentPointIndex < routePoints.size) {
			val dest = routePoints[currentPointIndex].first
			val gmmIntentUri = Uri.parse("google.navigation:q=${dest.latitude},${dest.longitude}")
			val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
			mapIntent.setPackage("com.google.android.apps.maps")
			if (mapIntent.resolveActivity(packageManager) != null) {
				startActivity(mapIntent)
			} else {
				Toast.makeText(this, "Google Maps não instalado.", Toast.LENGTH_SHORT).show()
			}
		}
	}

	private fun checkLocationPermission() {
		when {
			ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
				showUserLocation()
			}
			else -> {
				locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
			}
		}
	}

	private fun showUserLocation() {
		try {
			mMap.isMyLocationEnabled = true
		} catch (e: SecurityException) {
			Toast.makeText(this, "Erro de segurança ao mostrar localização.", Toast.LENGTH_SHORT).show()
		}
	}
}