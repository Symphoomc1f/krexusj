import request from '@/utils/request'

export function getMachineCmds(params) {
    let _currCommunity = JSON.parse(window.localStorage.getItem("curCommunity"));
    let communityId = '-1'
    if(_currCommunity != null && _currCommunity != undefined){
        communityId = _currCommunity.communityId;
    }
    return request({
        url: '/api/machine/getMachineCmds',
        method: 'get',
        params: {
            page: 1,
            row: 10,
            machineTypeCd: '9996',
            communityId:communityId
        }
    })
}

export function getMachineCmdsByCondition(params) {
    let _currCommunity = JSON.parse(window.localStorage.getItem("curCommunity"));

    if(_currCommunity != null && _currCommunity != undefined){
        params.communityId = _currCommunity.communityId;
    }else{
        params.communityId = "-1";
    }
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
    let _currCommunity = JSON.parse(window.localStorage.getItem("curCommunity"));

    if(_currCommunity != null && _currCommunity != undefined){
        params.communityId = _currCommunity.communityId;
    }else{
        params.communityId = "-1";
    }
    return request({
        url: '/api/machine/saveBarrierGateCmd',
        method: 'post',
        data:params
    })
}

export function getAccessControls(params) {
    let _currCommunity = JSON.parse(window.localStorage.getItem("curCommunity"));
    let communityId = '-1'
    if(_currCommunity != null && _currCommunity != undefined){
        communityId = _currCommunity.communityId;
    }
    return request({
        url: '/api/machine/getMachines',
        method: 'get',
        params: {
            page: 1,
            row: 10,
            machineTypeCd: '9996',
            communityId:communityId
        }
    })
}

export function getMachineCodes(params) {
    let _currCommunity = JSON.parse(window.localStorage.getItem("curCommunity"));

    if(_currCommunity != null && _currCommunity != undefined){
        params.communityId = _currCommunity.communityId;
    }else{
        params.communityId = "-1";
    }
    return request({
        url: '/api/machine/getMachineCodes',
        method: 'get',
        params
    })
}
export function getAccessControlsByCondition(params) {
    let _currCommunity = JSON.parse(window.localStorage.getItem("curCommunity"));

    if(_currCommunity != null && _currCommunity != undefined){
        params.communityId = _currCommunity.communityId;
    }else{
        params.communityId = "-1";
    }
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
    let _currCommunity = JSON.parse(window.localStorage.getItem("curCommunity"));

    if(_currCommunity != null && _currCommunity != undefined){
        params.communityId = _currCommunity.communityId;
    }else{
        params.communityId = "-1";
    }
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
    let _currCommunity = JSON.parse(window.localStorage.getItem("curCommunity"));

    if(_currCommunity != null && _currCommunity != undefined){
        params.communityId = _currCommunity.communityId;
    }else{
        params.communityId = "-1";
    }
    return request({
        url: '/api/machine/getMachineLogs',
        method: 'get',
        params
    })
}

export function getAccessControlFace(params) {
    let _currCommunity = JSON.parse(window.localStorage.getItem("curCommunity"));

    if(_currCommunity != null && _currCommunity != undefined){
        params.communityId = _currCommunity.communityId;
    }else{
        params.communityId = "-1";
    }
    return request({
        url: '/api/machine/getMachineFaces',
        method: 'get',
        params
    })
}

export function getMachineOpenDoors(params) {
    let _currCommunity = JSON.parse(window.localStorage.getItem("curCommunity"));

    if(_currCommunity != null && _currCommunity != undefined){
        params.communityId = _currCommunity.communityId;
    }else{
        params.communityId = "-1";
    }
    return request({
        url: '/api/machine/getMachineOpenDoors',
        method: 'get',
        params
    })
}

export function getTranLog(params) {
    let _currCommunity = JSON.parse(window.localStorage.getItem("curCommunity"));

    if(_currCommunity != null && _currCommunity != undefined){
        params.communityId = _currCommunity.communityId;
    }else{
        params.communityId = "-1";
    }
    return request({
        url: '/api/machine/getTranLogs',
        method: 'get',
        params
    })
}
