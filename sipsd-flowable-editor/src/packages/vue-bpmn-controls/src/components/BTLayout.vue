<template>
  <div id="bt-layout">
    <slot name="head">
    </slot>
    <slot name="right">
    </slot>
  </div>
</template>

<script>
export default {
name: "BTLayout"
}
</script>

<style scoped>
#bt-layout{
  position: fixed;
  right: 0;
  bottom: 30px;
  display: flex;
  align-items: flex-end;
}
</style>
