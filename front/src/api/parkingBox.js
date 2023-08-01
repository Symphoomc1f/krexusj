import request from '@/utils/request'

export function getParkingBoxs(params) {
    let _currCommunity = JSON.parse(window.localStorage.getItem("curCommunity"));

    if(_currCommunity != null && _currCommunity != undefined){
        params.communityId = _currCommunity.communityId;
    }else{
        params.communityId = "-1";
    }
    return request({
        url: '/api/parkingBox/getParkingBoxs',
        method: 'get',
        params
    })
}

export function deleteParkingBoxs(params) {
    return request({
        url: '/api/parkingBox/deleteParkingBox',
        method: 'post',
        data:params
    })
}

export function saveParkingBoxs(params) {
    let _currCommunity = JSON.parse(window.localStorage.getItem("curCommunity"));

    if(_currCommunity != null && _currCommunity != undefined){
        params.communityId = _currCommunity.communityId;
    }else{
        params.communityId = "-1";
    }
    return request({
        url: '/api/parkingBox/saveParkingBox',
        method: 'post',
        data:params
    })
}