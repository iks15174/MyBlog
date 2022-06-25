<template>
  <nav
    class="navbar navbar-expand-md navbar-light mb-0"
    :style="
      `background-color: ${VUE_APP_NAVBAR_BG_CSS_COLOR}; color: ${VUE_APP_NAVBAR_TEXT_CSS_COLOR};`
    "
  >
    <router-link
      class="navbar-brand"
      :to="'/'"
      :style="`color: ${VUE_APP_NAVBAR_TEXT_CSS_COLOR};`"
    >
      {{ title }}
    </router-link>

    <button
      class="navbar-toggler"
      type="button"
      data-toggle="collapse"
      data-target="#headerContent"
      aria-controls="headerContent"
      aria-expanded="false"
      :style="`color: ${VUE_APP_NAVBAR_TEXT_CSS_COLOR};`"
    >
      <i class="bi bi-list"></i>
    </button>

    <div class="collapse navbar-collapse" id="headerContent">
      <ul class="navbar-nav mr-auto">
        <li class="nav-item">
          <router-link
            class="nav-link"
            :to="'/'"
            :style="`color: ${VUE_APP_NAVBAR_TEXT_CSS_COLOR};`"
          >
            홈
          </router-link>
        </li>
        <li class="nav-item">
          <router-link
            class="nav-link"
            :to="'/'"
            :style="`color: ${VUE_APP_NAVBAR_TEXT_CSS_COLOR};`"
          >
            태그
          </router-link>
        </li>
        <li class="nav-item">
          <router-link
            class="nav-link"
            :to="'/'"
            :style="`color: ${VUE_APP_NAVBAR_TEXT_CSS_COLOR};`"
          >
            방명록
          </router-link>
        </li>
      </ul>
      <ul class="navbar-nav">
        <li class="nav-item dropdown-md">
          <a
            class="nav-link"
            href="#"
            id="navbarDropdown"
            role="button"
            data-toggle="dropdown"
            aria-expanded="false"
            :style="`color: ${VUE_APP_NAVBAR_TEXT_CSS_COLOR};`"
          >
            <i class="bi bi-list"></i>
          </a>
          <div class="dropdown-menu dropdown-menu-right" aria-labelledby="navbarDropdown">
            <router-link class="dropdown-item" :to="'/'">
              내정보
            </router-link>
            <router-link class="dropdown-item" :to="'/'">
              글작성
            </router-link>
          </div>
        </li>
      </ul>
    </div>
  </nav>
</template>

<script language="ts">
import { defineComponent } from 'vue'

const {
  VUE_APP_NAVBAR_BG_CSS_COLOR = 'black',
  VUE_APP_NAVBAR_TEXT_CSS_COLOR = 'white'
} = process.env

export default defineComponent({
  props: {
    title: {
      type: String,
      default: ''
    },
    sections: {
      type: Object,
      default: () => ({})
    }
  },
  setup () {
    return {
      VUE_APP_NAVBAR_BG_CSS_COLOR,
      VUE_APP_NAVBAR_TEXT_CSS_COLOR
    }
  }
})
</script>
