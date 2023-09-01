import request from '@/utils/request'

export function getCommunitys(params) {
    return request({
        url: '/api/community/getCommunitys',
        method: 'get',
        params: {
            page: 1,
            row: 10,
            communityId: ''
        }
    })
}

export function getCommunitysByCondition(params) {
    return request({
        url: '/api/community/getCommunitys',
        method: 'get',
        params
    })
}

export function deleteCommunitys(params) {
    return request({
        url: '/api/community/deleteCommunity',
        method: 'post',
        data:params
    })
}

export function saveCommunitys(params) {
    return request({
        url: '/api/community/saveCommunity',
        method: 'post',
        data:params
    })
}

export function updateCommunitys(params) {
    return request({
        url: '/api/community/updateCommunitys',
        method: 'post',
        data:params
    })
  }

export function getCityArea(params){
    return request({
        url: '/api/cityArea/getCityAreas',
        method: 'get',
        params
    })
}