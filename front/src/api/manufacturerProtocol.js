import request from '@/utils/request'

export function getProtocols(params) {
    return request({
        url: '/api/manufacturer/getManufacturers',
        method: 'get',
        params
    })
}
//http://localhost:8080/api/manufacturer/getManufacturers
//http://localhost:8080/api/manufacturer/getManufacturers?page=1&row=10&hmType=1001&hmName=&hmId=


export function startManufacturer(params) {
    return request({
        url: '/api/manufacturer/startManufacturer',
        method: 'post',
        data:params
    })
}