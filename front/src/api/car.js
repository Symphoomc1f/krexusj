import request from '@/utils/request'

export function queryCars(params) {
    let _currCommunity = JSON.parse(window.localStorage.getItem("curCommunity"));
    let communityId = '-1'
    if(_currCommunity != null && _currCommunity != undefined){
        communityId = _currCommunity.communityId;
        params.communityId = communityId
    }

 
    return request({
        url: '/api/car/getCars',
        method: 'get',
        params
    })
}

export function deleteCars(params) {
    return request({
        url: '/api/car/deleteCar',
        method: 'post',
        data:params
    })
}

export function getCarInouts(params) {
    let _currCommunity = JSON.parse(window.localStorage.getItem("curCommunity"));
    let communityId = '-1'
    if(_currCommunity != null && _currCommunity != undefined){
        communityId = _currCommunity.communityId;
        params.communityId = communityId
    }
    return request({
        url: '/api/car/getCarInouts',
        method: 'get',
        params
    })
}