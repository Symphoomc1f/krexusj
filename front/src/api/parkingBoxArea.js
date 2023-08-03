import request from '@/utils/request'

export function getParkingBoxAreas(params) {
    let _currCommunity = JSON.parse(window.localStorage.getItem("curCommunity"));

    if(_currCommunity != null && _currCommunity != undefined){
        params.communityId = _currCommunity.communityId;
    }else{
        params.communityId = "-1";
    }
    return request({
        url: '/api/parkingBoxArea/getParkingBoxAreas',
        method: 'get',
        params
    })
}

export function deleteParkingBoxAreas(params) {
    return request({
        url: '/api/parkingBoxArea/deleteParkingBoxArea',
        method: 'post',
        data:params
    })
}

export function saveParkingBoxAreas(params) {
    let _currCommunity = JSON.parse(window.localStorage.getItem("curCommunity"));

    if(_currCommunity != null && _currCommunity != undefined){
        params.communityId = _currCommunity.communityId;
    }else{
        params.communityId = "-1";
    }
    return request({
        url: '/api/parkingBoxArea/saveParkingBoxArea',
        method: 'post',
        data:params
    })
}


export function updateParkingBoxArea(params) {
    let _currCommunity = JSON.parse(window.localStorage.getItem("curCommunity"));

    if(_currCommunity != null && _currCommunity != undefined){
        params.communityId = _currCommunity.communityId;
    }else{
        params.communityId = "-1";
    }
    return request({
        url: '/api/parkingBoxArea/updateParkingBoxArea',
        method: 'post',
        data:params
    })
}