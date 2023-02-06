import request from '@/utils/request'

export function getParkingAreas(params) {
    let _currCommunity = JSON.parse(window.localStorage.getItem("curCommunity"));
    let communityId = '-1'
    if(_currCommunity != null && _currCommunity != undefined){
        communityId = _currCommunity.communityId;
    }
    params.communityId = communityId;
    return request({
        url: '/api/parkingArea/getParkingAreas',
        method: 'get',
        params
    })
}

export function deleteParkingArea(params) {
   
    return request({
        url: '/api/parkingArea/deleteParkingArea',
        method: 'post',
        data:params
    })
}

export function saveParkingArea(params) {
    let _currCommunity = JSON.parse(window.localStorage.getItem("curCommunity"));
    let communityId = '-1'
    if(_currCommunity != null && _currCommunity != undefined){
        communityId = _currCommunity.communityId;
    }
    params.communityId = communityId;
    return request({
        url: '/api/parkingArea/saveParkingArea',
        method: 'post',
        data:params
    })
}

export function updateParkingArea(params) {
    return request({
        url: '/api/parkingArea/updateParkingArea',
        method: 'post',
        data:params
    })
  }