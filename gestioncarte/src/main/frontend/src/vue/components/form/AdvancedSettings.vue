<template>
	<slot v-if="open"></slot>
	<Row v-else><a class="link-primary mb-2" role="button" @click="open = true">Avancé...</a></Row>
</template>

<script lang="ts" setup>
import Row from '@components/grid/Row.vue';
import { ref } from "vue";

const open = ref(false);
</script>
