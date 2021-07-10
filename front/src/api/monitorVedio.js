import request from '@/utils/request'

export function getMonitors(params) {
    return request({
        url: '/api/machine/getMachines',
        method: 'get',
        params: {
            page: 1,
            row: 10,
            machineTypeCd: '9996'
        }
    })
}

export function getMonitorsByCondition(params) {
    return request({
        url: '/api/machine/getMachines',
        method: 'get',
        params
    })
}

export function deleteMonitors(params) {
    return request({
        url: '/api/machine/deleteMachine',
        method: 'post',
        data:params
    })
}