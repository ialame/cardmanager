<template>
    <Collapse v-model:open="opened" :class="{'card mb-2': opened, 'new-event': !opened && isNil(event?.id)}">
        <template #label>
            <PromoCardEventLabel :event="event" />
        </template>
        <template #after-label>
            <FormButton color="link" class="ms-2" @click.stop="emit('remove')">
                <Icon name="trash" class="text-danger" />
            </FormButton>
        </template>
        <slot />
    </Collapse>
</template>

<script lang="ts" setup>
import {PromoCardEventLabel} from "@components/cards/promo";
import Collapse from "@components/collapse/Collapse.vue";
import {PromoCardEventDTO} from "@/types";
import {ref} from "vue";
import {isNil} from "lodash";
import FormButton from "@components/form/FormButton.vue";
import Icon from "@components/Icon.vue";

interface Props {
    event: PromoCardEventDTO;
}

interface Events {
    (e: 'remove'): void;
}

defineProps<Props>();
const emit = defineEmits<Events>();

const opened = ref(false);

</script>


<style lang="scss" scoped>
@import '@/variables.scss';

.new-event:deep(button.collapse-button) {
    background-color: rgba($line-green, 0.25);
}
</style>
