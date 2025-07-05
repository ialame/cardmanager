<template>
    <table v-if="modelValue?.promos?.length > 0 || modelValue?.existingPromos?.length > 0" class="table table-striped">
        <thead>
            <tr>
                <th>Extension</th>
                <th>Card</th>
                <th>Promo</th>
                <th>Version</th>
                <th></th>
            </tr>
        </thead>
        <tbody>
            <OnePiecePromoCardLine v-for="promo in modelValue.promos" class="new-promo" :key="promo.id" :promo="promo" @remove="removePromo(promo)" />
            <OnePiecePromoCardLine v-for="promo in modelValue.existingPromos" :key="promo.id" :promo="promo" @remove="removePromo(promo)" />
        </tbody>
    </table>
    <PromoCardEventEditForm v-if="editedEvent" v-model="editedEvent" :savedEvents="modelValue.savedEvents" :localizations="localizations" :distribution="distribution" @save="$emit('save')">
        <template #buttons>
            <FormButton class="mt-4 mb-4 me-2" color="secondary" @click="reload()" loading>Regenerer</FormButton>
        </template>
    </PromoCardEventEditForm>
</template>

<script lang="ts" setup>
import {PromoCardEventEditForm} from "@components/cards/promo";
import {ExtractedPromoCardEventDTO, PromoCardDTO, PromoCardEventDTO} from "@/types";
import {computed} from "vue";
import {cloneDeep, isEqual} from "lodash";
import {computedAsync} from "@vueuse/core";
import {LocalizationCode} from "@/localization";
import FormButton from "@components/form/FormButton.vue";
import rest from "@/rest";
import log from "loglevel";
import OnePiecePromoCardLine from "@components/cards/onepiece/promo/event/extraction/OnePiecePromoCardLine.vue";
import {onePieceSetService} from "@components/cards/onepiece/set";

interface Props {
    modelValue: ExtractedPromoCardEventDTO;
}

interface Events {
    (e: 'update:modelValue', value: ExtractedPromoCardEventDTO): void;
    (e: 'save'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Events>();

const editedEvent = computed<PromoCardEventDTO | undefined>({
    get: () => props.modelValue.event,
    set: v => {
        if (isEqual(v, props.modelValue.event)) {
            return;
        }

        const copy = cloneDeep(props.modelValue);

        copy.event = v;
        emit('update:modelValue', copy)
    }
});
const localizations = computedAsync<LocalizationCode[]>(async () => {
    const keys = Object.keys(props.modelValue.event?.translations);

    if (keys.length === 1) {
        return [keys[0] as LocalizationCode];
    }
    return props.modelValue.setIds ? [...new Set((await Promise.all(props.modelValue.setIds.map(i => onePieceSetService.get(i))))
        .flatMap(s => Object.values(s.translations))
        .filter(t => t.available)
        .map(t => t.localization))] : [];
});
const distribution = computedAsync(async () => {
    if (props.modelValue.promos) {
        return false;
    }

    return (await Promise.all(props.modelValue.promos
        .map(p => onePieceSetService.findWithPromo(p.id))))
        .some(c => c?.distribution);
}, false);
const removePromo = (promo: PromoCardDTO) => {
    const copy = cloneDeep(props.modelValue);

    copy.promos = copy.promos?.filter(p => p.id !== promo.id) ?? [];
    copy.existingPromos = copy.existingPromos?.filter(p => p.id !== promo.id) ?? [];
    log.debug("Remove promo", copy);
    emit('update:modelValue', copy);
};
const reload = async () => {
    const value = await rest.post("/api/cards/promos/events/extracted/reload", {
        data: props.modelValue
    });

    emit('update:modelValue', value);
}

</script>

<style lang="scss" scoped>
@import '@/variables.scss';

tr.new-promo:deep(td) {
    background-color: rgba($line-green, 0.25);
}
</style>
