import request from '@/utils/request'

export function queryCars(params) {
    return request({
        url: '/api/car/getCars',
        method: 'get',
        params: {
            page: 1,
            row: 10,
            params
        }
    })
}