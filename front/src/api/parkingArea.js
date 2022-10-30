import request from '@/utils/request'

export function getParkingAreas(params) {
    return request({
        url: '/api/parkingArea/getParkingAreas',
        method: 'get',
        params
    })
}

export function deleteParkingArea(params) {
    return request({
        url: '/api/parkingArea/deleteParkingArea',
        method: 'post',
        data:params
    })
}

export function saveParkingArea(params) {
    return request({
        url: '/api/parkingArea/saveParkingArea',
        method: 'post',
        data:params
    })
}

export function updateParkingArea(params) {
    return request({
        url: '/api/parkingArea/updateParkingArea',
        method: 'post',
        data:params
    })
  }