<template>
  <div>
    <div class="bt-layout-header">
      <slot name="head">
      </slot>
    </div>
    <div class="bt-layout-right">
      <slot name="right">
      </slot>
    </div>
  </div>

</template>

<script>
export default {
name: "BTLayout"
}
</script>

<style scoped>
.bt-layout-header{
  position: absolute;
  top: 6px;
  right: 20px;
}
.bt-layout-right{
  position: absolute;
  right: 0;
  top: 60px;
  bottom: 60px;
  display: flex;
  align-items: flex-end;
}
</style>
