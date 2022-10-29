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


export function saveMonitor(params) {
  return request({
    url: '/api/machine/saveMachine',
    method: 'post',
    data:params
  })
}

export function updateMonitor(params) {
  return request({
    url: '/api/machine/updateMonitor',
    method: 'post',
    data:params
  })
}
