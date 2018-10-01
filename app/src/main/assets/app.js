import Vue from "vue"
import "todomvc-app-css/index.css"
import TodoList from "./TodoList.vue"
import "./styles.css"

new Vue({
  el: '#app',
  render: h => h(TodoList),
});
