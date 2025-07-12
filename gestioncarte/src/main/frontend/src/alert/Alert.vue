<template>
    <div class="alert alert-dismissible fade show border border-1 mt-1" :class="alertTypeClass">
        <span>{{message}}</span>
        <button type="button" class="btn-close no-focus" aria-label="Close" @click="$emit('close')" />
    </div>
</template>

<script lang="ts" setup>
import {computed} from "vue";
import {AlertType} from "@/alert/logic";

interface Props {
    type: AlertType;
    message: string;
}

interface Emits {
    (e: 'close'): void;
}

const props = defineProps<Props>();
defineEmits<Emits>()

const alertTypeClass = computed(() => `alert-${props.type}`);

</script>
