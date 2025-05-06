package com.aura.mapsaura

import android.content.ContentValues.TAG
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.aura.mapsaura.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.*
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley

import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private  var MAP_ZOOM : Float = 10f
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.map_options, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.satellite_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.hybrid_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.terrain_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

        private fun setMapStyle(googleMap: GoogleMap) {
        try {
            val success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.maps_style))
            if(!success){
                Log.e(TAG,"Failed to find new map style")
            }
        } catch (e: Resources.NotFoundException){
            Log.e(TAG,"Maps Style using normal map")
        }
    }

    private fun setMapLongClick (googleMap: GoogleMap) {
        googleMap.setOnMapLongClickListener { latLng ->
            val snippet = String.format(Locale.getDefault(),
                "Lat: %.5f, Long: %.5f", latLng.latitude, latLng.longitude)
            val markerPin = BitmapDescriptorFactory.fromResource(R.drawable.marker2)
            googleMap.addMarker(MarkerOptions().position(latLng).title("Drop Pin Marker")
                .snippet(snippet)
                .icon(markerPin))
        }
    }

    private fun getMarkerApi() {
        val url = "http://10.0.2.2/backend_ecommerce/allmap.php"
        val queue = Volley.newRequestQueue(this@MapsActivity)
        val request = JsonArrayRequest(Request.Method.GET, url, null, { response ->

            try {
                for (i in 0 until response.length()) {
                    val location = response.getJSONObject(i)
                    val title = location.getString("title")
                    val latitude = location.getDouble("latitude")
                    val longitude = location.getDouble("longitude")
                    val snippet = location.getString("snippet")
                    val latLng = LatLng(latitude, longitude)
                    mMap.addMarker(MarkerOptions()
                        .title(title)
                        .position(latLng)
                        .snippet(snippet)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
                }
                if (response.length() > 0){
                    val location = response.getJSONObject(0)
                    val latitude = location.getDouble("latitude")
                    val longitude = location.getDouble("longitude")
                    mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(latitude, longitude), 10f
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, { error ->
            Toast.makeText(
                this@MapsActivity,
                "Gagal mendapatkan response dari server",
                Toast.LENGTH_LONG
            ).show()
        })
        queue.add(request)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        setMapStyle(googleMap)
        setMapLongClick(googleMap)
        getMarkerApi()
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val padang = LatLng(-0.937480, 100.360275)
        mMap.addMarker(MarkerOptions().position(padang).title("Kantor Gubernur SUMBAR"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(padang, MAP_ZOOM))
        val rsup = LatLng(-0.9431736329556296, 100.3669706956101)
        var markerRsup: Marker?=null
        markerRsup = mMap.addMarker(MarkerOptions().position(rsup).title("RSUD M.Djamil Padang")
            .snippet("Rumah Sakit Umum Terbesar di Sumatera Tengah").
        icon(BitmapDescriptorFactory.fromResource(R.drawable.marker2))
        )
        markerRsup?.tag =0
        val apotik = LatLng(-0.95027461, 100.367101950)
        var markerApotik: Marker?=null
        markerApotik = mMap.addMarker(MarkerOptions().position(apotik).title("Apotik Kimia Farma")
            .snippet("Apotik terlengkap di kota Padang").
            icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)))
        markerRsup?.tag =0
        val gor = LatLng(-0.9286944225484136, 100.35891392022484)
        var markerGor: Marker?=null
        markerGor = mMap.addMarker(MarkerOptions().position(gor).title("GOR H.Agus Salim Padang")
            .snippet("Tempat olahraga terbesar di kota padang").
            icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
        markerRsup?.tag =0
        val rsu = LatLng(-0.9442583569759891, 100.36772037009781)
        var markerRsu: Marker?=null
        markerRsu = mMap.addMarker(MarkerOptions().position(rsu).title("RSU BUNDA BMC Padang")
            .snippet("RSU Swasta di Kota Padang").
            icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)))
        markerRsup?.tag =0
        val masjid = LatLng(-0.896328351720092, 100.3623605435629)
        var markerMasjid: Marker?=null
        markerMasjid = mMap.addMarker(MarkerOptions().position(masjid).title("Masjid al-Hakim Padang")
            .snippet("Masjid yang artistik di kota Padang").
            icon(BitmapDescriptorFactory.fromResource(R.drawable.marker3)))
        markerRsup?.tag =0
    }


}