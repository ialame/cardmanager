<template>
    <FormButton v-bind="$attrs" color="secondary" @click.stop="upload">
        <Icon class="v-flip" src="/svg/download.svg" />
    </FormButton>
</template>

<script lang="ts" setup>
import FormButton from "@components/form/FormButton.vue";
import Icon from "@components/Icon.vue";

interface Props {
    accept?: string;
}

interface Emits {
    (e: 'upload', content: ArrayBuffer): void;
}

const props = withDefaults(defineProps<Props>(), {
    accept: '.json',
});
const emit = defineEmits<Emits>();

const upload = async () => {
    const input = document.createElement('input');
    
    input.type = 'file';
    input.accept = props.accept;
    input.onchange = async () => {
        if (input.files && input.files.length) {
            const f = input.files[0];
            const reader = new FileReader();

            reader.onload = async () => emit('upload', reader.result as ArrayBuffer);
            reader.readAsArrayBuffer(f);
        }
    };
    input.click();
}
</script>
