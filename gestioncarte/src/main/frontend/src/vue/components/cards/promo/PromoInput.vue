<template>
    <AdvancedFormInput :path="concatPaths([path, 'name'])">
        <template #before>
            <AdvancedFormCheckbox :path="concatPaths([path, 'used'])" type="radio" :slider="false" />
        </template>
        <template #after>
            <template v-if="!readOnly">
                <FormButton v-if="eventId" color="secondary" class="form-btn ms-2" @click="editEvent">
                    <Icon name="calendar-outline" />
                </FormButton>
                <FormButton v-else color="secondary" class="form-btn ms-2" @click="editEvent">
                    <Icon name="calendar-clear-outline" />
                </FormButton>
            </template>
        </template>
    </AdvancedFormInput>
</template>

<script setup lang="ts">
import {concatPaths, Path} from "@/path";
import {AdvancedFormCheckbox, AdvancedFormInput, useAdvancedFormInput} from "@components/form/advanced";
import FormButton from "@components/form/FormButton.vue";
import Icon from "@components/Icon.vue";
import {PromoCardDTO} from "@/types";
import {computed} from "vue";
import {useRouter} from "vue-router";

interface Props {
    path: Path;
    tcg: string;
}

const props = defineProps<Props>();
const router = useRouter();

const { value, readOnly } = useAdvancedFormInput<any, PromoCardDTO>(() => props.path);

const eventId = computed(() => value.value?.eventId);

const editEvent = async () => {
    if (!eventId.value) {
        await router.push({
            path: `/cards/${props.tcg}/promos/events/extract/`,
            query: {from: value.value?.id}
        });
    } else {
        await router.push(`/cards/${props.tcg}/promos/events/${eventId.value}`);
    }
}
</script>
