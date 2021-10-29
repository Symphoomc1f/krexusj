import request from '@/utils/request'

export function getAttendances(params) {
    return request({
        url: '/api/machine/getMachines',
        method: 'get',
        params: {
            page: 1,
            row: 10,
            machineTypeCd: '9997'
        }
    })
}

export function getAttendancesByCondition(params) {
    return request({
        url: '/api/machine/getMachines',
        method: 'get',
        params
    })
}

export function deleteAttendances(params) {
    return request({
        url: '/api/machine/deleteMachine',
        method: 'post',
        data:params
    })
}

export function restartAttendances(params) {
    return request({
        url: '/api/machine/startMachine',
        method: 'post',
        data:params
    })
}


export function getAttendanceFace(params) {
    return request({
        url: '/api/machine/getMachineFaces',
        method: 'get',
        params
    })
}
