<template>
  <div>
    <div>
        <input v-model="newTitle"/><input value="create" type="button" @click="addTodo"/>
    </div>
    You have {{todos.length}} things to do:
    <ul>
      <li v-for="todo in todos">
        <input type="checkbox" :checked=todo.completed @click="toggle(todo)"/>
        {{ todo.title }}
      </li>
    </ul>
  </div>
</template>

<script type = "text/javascript" >

import axios from 'axios'

export default {
  name: 'TodoList',
  data: function(){return {todos: [], newTitle: ""}},
  methods: {
    addTodo: function(){
        axios.post('/api/todos', {"title": this.newTitle, })
             .then(response => {
                 this.newTitle=""
             })
             .catch( e => { alert(e); })
    },
    toggle: function(todo){
        axios.put('/api/todos/'+todo.id, {"title": todo.title, "completed": !todo.completed})
             .catch( e => { alert(e); })
    },
    loadTodos: function(){
                   axios.get(`/api/todos`)
                        .then(response => {
                                   this.todos = response.data
                        })
                        .catch(e => { alert(e); })
                 }
  },
  created: function(){
    this.loadTodos()

    setInterval(this.loadTodos, 1000)
  }
}

</script>
<style>
</style>


