import request from '@/utils/request'

export function getMachineCmds(params) {
    return request({
        url: '/api/machine/getMachineCmds',
        method: 'get',
        params: {
            page: 1,
            row: 10,
            machineTypeCd: '9999'
        }
    })
}

export function getMachineCmdsByCondition(params) {
    return request({
        url: '/api/machine/getMachineCmds',
        method: 'get',
        params
    })
}

export function deleteMachineCmd(params) {
    return request({
        url: '/api/machine/deleteMachineCmd',
        method: 'post',
        data:params
    })
}

export function saveMachineCmd(params) {
    return request({
        url: '/api/machine/saveBarrierGateCmd',
        method: 'post',
        data:params
    })
}

export function getAccessControls(params) {
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

export function getMachineCodes(params) {
    return request({
        url: '/api/machine/getMachineCodes',
        method: 'get',
        params
    })
}
export function getAccessControlsByCondition(params) {
    return request({
        url: '/api/machine/getMachines',
        method: 'get',
        params
    })
}

export function deleteAccessControls(params) {
    return request({
        url: '/api/machine/deleteMachine',
        method: 'post',
        data:params
    })
}

export function saveAccessControls(params) {
    return request({
        url: '/api/machine/saveBarrierGate',
        method: 'post',
        data:params
    })
}

export function restartAccessControls(params) {
    return request({
        url: '/api/machine/startMachine',
        method: 'post',
        data:params
    })
}

export function getAccessControlsLog(params) {
    return request({
        url: '/api/machine/getMachineLogs',
        method: 'get',
        params
    })
}

export function getAccessControlFace(params) {
    return request({
        url: '/api/machine/getMachineFaces',
        method: 'get',
        params
    })
}

export function getMachineOpenDoors(params) {
    return request({
        url: '/api/machine/getMachineOpenDoors',
        method: 'get',
        params
    })
}

export function getTranLog(params) {
    return request({
        url: '/api/machine/getTranLogs',
        method: 'get',
        params
    })
}
