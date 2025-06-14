package dev.aleksrychkov.geoghost.feature.map.provider.libre.internal

import org.maplibre.android.geometry.LatLng

internal val defaultStatLatLng: Array<LatLng> by lazy {
    arrayOf(
        LatLng(35.6895, 139.6917),  // Tokyo, Japan
        LatLng(28.6139, 77.2090),   // Delhi, India
        LatLng(39.9042, 116.4074),  // Beijing, China
        LatLng(-23.5505, -46.6333), // São Paulo, Brazil
        LatLng(40.7128, -74.0060),  // New York City, USA
        LatLng(13.7563, 100.5018),  // Bangkok, Thailand
        LatLng(55.7558, 37.6173),    // Moscow, Russia
        LatLng(-12.0464, -77.0428), // Lima, Peru
        LatLng(51.5074, -0.1278),  // London, UK
        LatLng(48.8566, 2.3522),   // Paris, France
        LatLng(55.7558, 37.6173),  // Moscow, Russia
        LatLng(41.9028, 12.4964),  // Rome, Italy
        LatLng(52.5200, 13.4050)   // Berlin, Germany
    )
}
