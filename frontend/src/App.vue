<template>
  <PatchMeta />
  <NavBar :title="'📝 iks15174'" :sections="blogSections" />
  <div class="row flex-xl-nowrap">
    <SideBar />
    <div class ="col-12 col-md-9 col-xl-8 py-md-3 pl-md-5 content">
      <router-view />
    </div>
  </div>
  <Loader />
  <Footer />
</template>

<script lang="ts">
import { defineComponent, inject } from 'vue'
import PatchMeta from '@/components/PatchMeta.vue'
import Loader from '@/components/Loader.vue'
import NavBar from '@/components/NavBar.vue'
import SideBar from '@/components/SideBar.vue'
import Footer from '@/components/Footer.vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
import 'bootstrap-icons/font/bootstrap-icons.css'
import 'github-markdown-css/github-markdown.css'

export default defineComponent({
  components: {
    PatchMeta,
    Loader,
    NavBar,
    Footer,
    SideBar
  },
  setup () {
    const blogSections = inject('blogSections', {})
    return { blogSections }
  }
})
</script>
