import request from '@/utils/request'

export function getBarrierGates(params) {
    return request({
        url: '/api/machine/getMachines',
        method: 'get',
        params: {
            page: 1,
            row: 10,
            machineTypeCd: '9999'
        }
    })
}

export function getBarrierGatesByCondition(params) {
    return request({
        url: '/api/machine/getMachines',
        method: 'get',
        params
    })
}

export function deleteBarrierGates(params) {
    return request({
        url: '/api/machine/deleteMachine',
        method: 'post',
        data:params
    })
}