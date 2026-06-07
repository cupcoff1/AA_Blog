<template>
  <div class="not-found">
    <h1>404</h1>
    <p>页面不存在</p>
    <RouterLink to="/">返回首页</RouterLink>
  </div>
</template>

<style scoped>
.not-found {
  text-align: center;
  padding: 5rem 0;
}

.not-found h1 {
  font-size: 4em;
  color: var(--text-secondary);
  margin-bottom: 0.5rem;
}

.not-found a {
  display: inline-block;
  margin-top: 1.5rem;
}
</style>
