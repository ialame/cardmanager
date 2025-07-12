<template>
    <div class="container">
        <PromoCardEventEditForm v-model="event" @save="save()" />
    </div>
</template>

<script lang="ts" setup>
import {PromoCardEventEditForm, promoCardEventService} from "@components/cards/promo/event";
import {useRoute, useRouter} from "vue-router";
import {computed, ref, watchEffect} from "vue";
import {ExtractedPromoCardEventDTO, PromoCardEventDTO} from "@/types";
import rest from "@/rest";
import {usePageTitle} from "@/vue/composables/PageComposables";
import {useRaise} from "@/alert";


const raise = useRaise();
const route = useRoute();
const router = useRouter();
const eventId = computed(() => route.params.eventId as string || '');
const tcg = computed(() => route.params.tcg as string || '');
const event = ref<PromoCardEventDTO>({
    id: "",
    name: "",
    hidden: false,
    championship: false,
    withoutDate: false,
    alwaysDisplayed: false,
    translations: {},
    traits: [],
});

usePageTitle(computed(() => {
    const edit = event.value.id;
    const end = edit ? ` (id: ${event.value.id})` : '';

    return `${edit? 'Modification' : 'Ajout'} d'évenemnt promotionel${end}`;
}));

const save = async () => {
    if (!event.value) {
        return;
    }
    const e = await promoCardEventService.save(event.value);

    await router.push(`/cards/promos/events/${e.id}`);
    raise.success("L'évènement a été sauvegardé");
}

watchEffect(async () => {
    const from = route.query["from-extraction"];

    if (from) {
        const extracted = await rest.get(`/api/cards/promos/events/extracted/${from}`) as ExtractedPromoCardEventDTO;

        if (extracted && extracted.event) {
            event.value = extracted.event;
            if (event.value.id) {
                await router.replace({path: `/cards/promos/events/${event.value.id}`, query: { "from-extraction": from }});
            }
            return;
        }
    }
    if (eventId.value) {
        event.value = await promoCardEventService.get(eventId.value);
    }
}, {flush: 'post'});
</script>
