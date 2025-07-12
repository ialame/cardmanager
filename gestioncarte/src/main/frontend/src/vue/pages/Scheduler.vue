<template>
    <SchedulerEntry v-for="task in tasks" :name="task" :key="task"></SchedulerEntry>
</template>

<script lang="ts" setup>
import rest from '@/rest';
import SchedulerEntry from '@components/SchedulerEntry.vue';
import {onMounted, ref} from 'vue';
import {usePageTitle} from "@/vue/composables/PageComposables";

usePageTitle("Scheduler");

const tasks = ref<string[]>([]);

onMounted(() => rest.get('/api/scheduler/tasks', {
	success: data => tasks.value = data
}));
</script>
