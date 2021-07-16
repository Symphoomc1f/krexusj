import request from '@/utils/request'

export function getMappings(params) {
    return request({
        url: '/api/mapping/getMappings',
        method: 'get',
        params: {
            page: 1,
            row: 10,
            id: ''
        }
    })
}

export function getMappingsByCondition(params) {
    return request({
        url: '/api/mapping/getMappings',
        method: 'get',
        params
    })
}

export function deleteMappings(params) {
    return request({
        url: '/api/mapping/deleteMapping',
        method: 'post',
        data:params
    })
}

export function saveMappings(params) {
    return request({
        url: '/api/mapping/saveMapping',
        method: 'post',
        data:params
    })
}

export function updateMappings(params) {
    return request({
        url: '/api/mapping/updateMapping',
        method: 'post',
        data:params
    })
}

export function freshMappings(params) {
    return request({
        url: '/api/mapping/freshMapping',
        method: 'post',
        data:params
    })
}