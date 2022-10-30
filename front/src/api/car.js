import request from '@/utils/request'

export function queryCars(params) {
    return request({
        url: '/api/car/getCars',
        method: 'get',
        params
    })
}

export function deleteCars(params) {
    return request({
        url: '/api/car/deleteCar',
        method: 'post',
        data:params
    })
}

export function getCarInouts(params) {
    return request({
        url: '/api/car/getCarInouts',
        method: 'get',
        params
    })
}