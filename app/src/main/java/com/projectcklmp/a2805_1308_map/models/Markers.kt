package com.projectcklmp.a2805_1308_map.models;

import com.google.android.gms.maps.model.LatLng


class Markers {
    var user: String? = null
    var latLng: LatLng? = null

    constructor() {
    }

    constructor(author: String, body: String, time: String) {
        this.user = user
        this.latLng = latLng
    }

}


