<template>
  <div class="hello">
    <img alt="Vue logo" :src="require('../assets/logo.png')" :style="{'width':size}">
    <h1>{{msg}}</h1>
  </div>
</template>

<script>
export default {
  name: 'IconView',
  props: {
    msg: String,
    size:{
      type: String,
      default:"160px"
    }
  }
}
</script>

<style scoped>
.hello {
  text-align: center;
}
.hello img{
  width: 160px;
}
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
</style>
