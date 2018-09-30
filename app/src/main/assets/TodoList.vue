<template>
  <div>
    <div class="newTodo">
        <div class="title">todos</div>
        <input v-model="newTitle" v-on:keyup.enter="addTodo"/>
    </div>
    <div class="todolist">
        <span>You have {{ incomplete }} {{ pluralized }} to do</span>
        <ul>
          <li v-for="todo in todos">
            <input type="checkbox" class="completed-check" :checked=todo.completed @click="toggle(todo)"/>
            <span v-bind:class="{completed:todo.completed}">{{ todo.title }}</span>
          </li>
        </ul>
     </div>
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
  },
  computed:  { incomplete: function(){ return this.todos.filter(function(t){ return !t.completed}).length },
               pluralized: function(){ return (this.incomplete == 1 ? "thing" : "things") }}
}

</script>

<style>

body {
    background-color: #EEE;
}

.title {
    font-size: 100px;
    color: rgba(175, 47, 47, 0.15);
}

.newTodo {
  margin: 50px;
}

.newTodo > input {
  box-shadow: 0 0 30px #888;
  border-radius: 5px;
  display: block;
  width: 100%;
}

.todolist {
    margin: 50px;
    box-shadow: 0 0 30px #888;
    background: #F8F8F8;
    border-radius: 5px;
}

.todolist > span {
    padding: 8px;
    color: #444;
}

.todolist > ul {
    list-style: none;
    padding: 0;
}

.todolist > ul > li {
    border-top: 1px solid #888;
    padding: 4px;
    background-color: #F8F8F8;
    font-size: 120%;
}

.completed-check {
    margin: 0 10px;
}

.completed {
    color: #AAA;
    text-decoration: line-through;
}

</style>


