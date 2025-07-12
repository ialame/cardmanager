<template>
    <Loading :ready="!!event">
        <div class="full-screen-container">
            <ExtractedPokemonPromoEvent v-if="tcg == 'pokemon'" v-model="event" @save="save()" />
            <ExtractedOnePiecePromoEvent v-else-if="tcg == 'onepiece'" v-model="event" @save="save()" />
        </div>
    </Loading>
</template>

<script lang="ts" setup>
import {computed, ref, watch} from "vue";
import {useRaise} from "@/alert";
import Loading from "@components/Loading.vue";
import {usePageTitle} from "@/vue/composables/PageComposables";
import {savePromoEvent} from "@components/cards/promo/event/logic";
import {ExtractedPromoCardEventDTO} from "@/types";
import rest from "@/rest";
import {useRoute, useRouter} from "vue-router";
import ExtractedPokemonPromoEvent
    from "@components/cards/pokemon/promo/event/extraction/ExtractedPokemonPromoEvent.vue";
import ExtractedOnePiecePromoEvent
    from "@components/cards/onepiece/promo/event/extraction/ExtractedOnePiecePromoEvent.vue";

const raise = useRaise();
const route = useRoute();
const router = useRouter();

const tcg = computed(() => route.params.tcg);

usePageTitle(`Promotion sans evenement`);

const event = ref<ExtractedPromoCardEventDTO>();

const save = async () => {
    if (!event.value) {
        return;
    }
    const e = await savePromoEvent(event.value);

    await router.push(`/cards/${tcg.value}/promos/events/${e.id}`);
    raise.success("L'évènement a été sauvegardé");
}

watch(useRoute(), async (route) => {
    const from = route.query.from;

    if (!from) {
        return;
    }
    event.value = (await rest.get(`/api/cards/promos/events/extract`, {
        params: {from}
    }))[0] as ExtractedPromoCardEventDTO;
}, {immediate: true});

</script>
