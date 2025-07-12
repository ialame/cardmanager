<template>
    <Checkbox v-model="value" :slider="false" />
</template>

<script lang="ts" setup>
import Checkbox from "@components/form/Checkbox.vue";
import {useSelection} from "@/selection";
import {computed} from "vue";

interface Props {
    entry: any;
}
const props = defineProps<Props>();

const { isSelected, select } = useSelection<any>();

const value = computed({
    get: () => isSelected(props.entry),
    set: (v) => select(props.entry, v)
});

</script>
