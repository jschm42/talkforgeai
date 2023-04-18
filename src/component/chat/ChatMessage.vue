<template>
  <div :class="messageClass" class="card p-1 m-2 shadow">
    <div class="row">
      <div class="col-md-1">
        <i :class="avatarImageClass" class="bi icon"></i>
      </div>
      <div class="col-md-11">
        <div class="card-body">
          <div class="card-text" v-html="message.content"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

import Role from '../../service/to/role';

export default {
  name: 'ChatMessage',
  props: {
    message: {
      role: String,
      content: String,
    },
  },
  computed: {
    messageClass() {
      return {
        'bg-info': this.message.role === Role.USER,
      };
    },
    isAssistant() {
      return this.message.role === Role.ASSISTANT;
    },
    isUser() {
      return this.message.role === Role.USER;
    },
    avatarImageClass() {
      return {
        'bi-robot': this.message.role === Role.ASSISTANT,
        'bi-person-fill': this.message.role === Role.USER,
      };
    },
  },
};
</script>

<style scoped>
.role-user {

}

.role-assistant {

}

.icon {
  font-size: 2em;
}
</style>
