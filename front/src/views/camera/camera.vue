<template>
  <div class="app-container">
    <div>
      <video class="video"  id="myPlayer" :src="videoURL" controls autoplay></video>
      <video class="video" id="myPlayer1" :src="videoURL" controls autoplay></video>
      <video class="video" id="myPlayer2" :src="videoURL" controls autoplay></video>
      <video class="video" id="myPlayer3" :src="videoURL" controls autoplay></video>
      <video class="video" id="myPlayer4" :src="videoURL" controls autoplay></video>
      <video class="video" id="myPlayer5" :src="videoURL" controls autoplay></video>
      <video class="video" id="myPlayer6" :src="videoURL" controls autoplay></video>
      <video class="video" id="myPlayer7" :src="videoURL" controls autoplay></video>
      <video class="video" id="myPlayer8" :src="videoURL" controls autoplay></video>
    </div>
  </div>
</template>

<script>
  import ezuikit from  "../../camera/ezuikit.js";
export default {
  data() {
    return {
      playerInstance: null,
      videoURL:'http://hls.open.ys7.com/openlive/f01018a141094b7fa138b9d0b856507b.m3u8',
      player:''
    }
  },
  watch: {

  },
  created() {
    this.initVideo();
  },
  methods: {
    initVideo:function () {
      setTimeout(function () {
        new ezuikit.EZUIPlayer('myPlayer');
        new ezuikit.EZUIPlayer('myPlayer1');
        new ezuikit.EZUIPlayer('myPlayer2');
        new ezuikit.EZUIPlayer('myPlayer3');
        new ezuikit.EZUIPlayer('myPlayer4');
        new ezuikit.EZUIPlayer('myPlayer5');
        new ezuikit.EZUIPlayer('myPlayer6');
        new ezuikit.EZUIPlayer('myPlayer7');
        new ezuikit.EZUIPlayer('myPlayer8');
      },2)
    }
  }
}
</script>

<style>
  .video{
    width: 360px;
    height: auto;
  }
</style>
