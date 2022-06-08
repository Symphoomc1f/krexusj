import request from '@/utils/request'


export function getCommunityPersons(params) {
    let _currCommunity = JSON.parse(window.localStorage.getItem("curCommunity"));

    if(_currCommunity != null && _currCommunity != undefined){
        params.communityId = _currCommunity.communityId;
    }else{
        params.communityId = "-1";
    }
    return request({
        url: '/api/communityPerson/getCommunityPersons',
        method: 'get',
        params
    })
}

export function deleteCommunityPersons(params) {
    return request({
        url: '/api/communityPerson/deleteCommunityPerson',
        method: 'post',
        data:params
    })
}

export function saveCommunityPersons(params) {
    let _currCommunity = JSON.parse(window.localStorage.getItem("curCommunity"));

    if(_currCommunity != null && _currCommunity != undefined){
        params.communityId = _currCommunity.communityId;
    }else{
        params.communityId = "-1";
    }
    return request({
        url: '/api/communityPerson/saveCommunityPerson',
        method: 'post',
        data:params
    })
}

export function getBase64(file) {
    return new Promise(function(resolve, reject) {
      let reader = new FileReader();
      let imgResult = "";
      reader.readAsDataURL(file);
      reader.onload = function() {
        imgResult = reader.result;
      };
      reader.onerror = function(error) {
        reject(error);
      };
      reader.onloadend = function() {
        resolve(imgResult);
      };
    });
  }

