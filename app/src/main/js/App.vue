<template>
  <div id="app" class="container">
    <div class="row">
      <img src="./assets/logo.png">
      <h1>{{ msg }}</h1>
    </div>
    <div class="row">
      <div class="col">
        <input type="text" class="form-control" placeholder="What needs to be done?">
      </div>
    </div>
    <div class="row">
      <div class="col">
        <table class="table table-bordered">
          <tbody>
          <tr>
            <td>(checkbox!)</td><td>Some task</td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'app',
  data () {
    return {
      msg: 'Welcome to Your Vue.js App'
    }
  }
}
</script>

<style lang="scss">
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}

h1, h2 {
  font-weight: normal;
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
