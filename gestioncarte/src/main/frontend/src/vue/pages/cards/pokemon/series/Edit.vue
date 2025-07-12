<template>
  <PokemonSerieEditForm v-model="serie" @save="save" />
</template>

<script lang="ts" setup>
import {useRoute, useRouter} from "vue-router";
import {usePageTitle} from "@/vue/composables/PageComposables";
import {computed, ref, watch} from "vue";
import rest from "@/rest";
import {useRaise} from "@/alert";

import {PokemonSerieEditForm, pokemonSerieService} from "@components/cards/pokemon/serie";
import {PokemonSerieDTO} from "@/types";


const route = useRoute();
const router = useRouter();
const raise = useRaise();
const serie = ref<PokemonSerieDTO>();


usePageTitle(computed(() => {
  const id = serie.value?.id;
  const end = id ? ` (id: ${id})` : '';

  return `Pokemon - ${id ? 'Modification' : 'Ajout'} de serie${end}`;
}));

const save = async () => {
  if (serie.value) {
    serie.value = await pokemonSerieService.save(serie.value);

    const pid = route.query['from-pid'];
    const localization = route.query['from-localization'];

    if (pid && localization) {
      await router.push(`/cards/pokemon/sets/new?from-pid=${pid}&from-localization=${localization}`);
      raise.info("Vous avez été redirigé vers la création de l'extension.");
    } else {
      await router.push(`/cards/pokemon/series/${serie.value.id}`);
    }
    raise.success("Serie enregistrée avec succès.");
  }
};

watch(route, async route => {
  const pid = route.query['from-pid'];
  const localization = route.query['from-localization'];

  if (pid && localization) {
    serie.value = await rest.get(`/api/cards/pokemon/official-site/series/${pid}/${localization}/`) as PokemonSerieDTO;
  } else if (route.params.serieId) {
    serie.value = await pokemonSerieService.get(route.params.serieId as string);
  } else {
    serie.value = {
      id: "",
      translations: {},
    };
  }
}, { immediate: true });

</script>
