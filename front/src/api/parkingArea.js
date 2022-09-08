import request from '@/utils/request'

export function getParkingAreas(params) {
    return request({
        url: '/api/parkingarea/getParkingAreas',
        method: 'get',
        params
    })
}

export function deleteParkingAreas(params) {
    return request({
        url: '/api/parkingarea/deleteParkingArea',
        method: 'post',
        data:params
    })
}

export function saveParkingAreas(params) {
    return request({
        url: '/api/parkingarea/saveParkingArea',
        method: 'post',
        data:params
    })
}