<template>
  <div>
    <router-view v-if="$route.path === '/login'" />
    <div v-else>
      <Layout>
        <router-view />
      </Layout>
    </div>
  </div>
</template>

<script setup>
import Layout from './views/Layout.vue'
</script>
<style>
html,
body {
  height: 100%;
  margin: 0;
  padding: 0;
  overflow: hidden;
  /* Prevent scrollbars */
}
</style>