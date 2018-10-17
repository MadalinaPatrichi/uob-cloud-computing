<template>
    <section class="todoapp">
        <div v-if="authenticated === false" style="padding: 10px 15px">
            <a href="/login">Login</a>
        </div>
        <div v-else>
            <header>
                <h1>todos</h1>
            </header>
            <section class="user-bar" style="padding: 10px 15px">
                <span>
                    Logged in as {{ user.name }}.
                </span>
            </section>
            <section class="input-bar">
                <input class="new-todo" v-model="newTitle" v-on:keyup.enter="addTodo" placeholder="What needs to be done?" />
            </section>
            <section class="main" v-show="todos.length">
                <ul class="todo-list">
                    <li v-for="todo in todos" class="todo" :class="{completed: todo.completed}">
                        <div class="view">
                            <input type="checkbox" class="toggle" :checked=todo.completed @click="toggle(todo)"/>
                            <label v-bind:class="{completed:todo.completed}">{{ todo.title }}</label>
                            <button class="destroy" @click="removeTodo(todo)"></button>
                        </div>
                    </li>
                </ul>
            </section>
            <footer class="footer">
                <span>You have {{ incomplete }} {{ pluralized }} to do</span>
            </footer>
        </div>
    </section>
</template>

<script type = "text/javascript" >
    import axios from 'axios'

    export default {
        name: 'TodoList',
        data: function () {
            return {
                todos: [],
                newTitle: "",
                user: null,
                authenticated: false,
            }
        },
        methods: {
            addTodo: function () {
                axios.post('/api/todos', {"title": this.newTitle,})
                    .then(response => {
                        this.newTitle = ""
                    })
                    .catch(e => {
                        alert(e);
                    })
            },
            toggle: function (todo) {
                axios.put('/api/todos/' + todo.id, {"title": todo.title, "completed": !todo.completed})
                    .catch(e => {
                        alert(e);
                    });
                this.loadTodos()
            },
            removeTodo: function(todo) {
                axios.delete('/api/todos/' + todo.id)
                    .catch(e => {
                        alert(e);
                    });
                this.loadTodos()
            },
            loadTodos: function () {
                axios.get(`/api/todos`)
                    .then(response => {
                        this.todos = response.data
                    })
                    .catch(e => {
                        alert(e);
                    })
            }
        },
        created: function () {
            axios.get("/api/user")
                .then(response => {
                    this.user = response.data;
                    this.authenticated = true;
                    axios.defaults.headers.common["X-CSRF-TOKEN"] = document.querySelector("#token").getAttribute("content");
                    axios.defaults.headers.common["Authorization"] = "Bearer " + this.user.bearerToken;
                    this.loadTodos();
                    setInterval(this.loadTodos, 1000)
                })
                .catch(e => {
                    this.user = null;
                    this.authenticated = false;
                })
        },
        computed: {
            incomplete: function () {
                return this.todos.filter(function (t) {
                    return !t.completed
                }).length
            },
            pluralized: function () {
                return (this.incomplete === 1 ? "thing" : "things")
            },
        }
    }
</script>
