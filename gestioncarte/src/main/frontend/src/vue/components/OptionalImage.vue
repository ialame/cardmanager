<template>
    <object v-if="src" :data="src" type="image/png" />
</template>

<script lang="ts" setup>
interface Props {
	src?: string;
}

defineProps<Props>();
</script>
