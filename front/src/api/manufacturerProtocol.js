import request from '@/utils/request'

export function getProtocols(params) {
    return request({
        url: '/api/manufacturer/getManufacturers',
        method: 'get',
        params
    })
}



export function startManufacturer(params) {
    return request({
        url: '/api/manufacturer/startManufacturer',
        method: 'post',
        data:params
    })
}