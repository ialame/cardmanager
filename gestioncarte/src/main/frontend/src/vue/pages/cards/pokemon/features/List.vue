<template>
    <SideButtons>
        <ScrollToTop />
    </SideButtons>
    <div class="container" v-if="!isEmpty(features)">
        <table class="table table-striped mt-4">
            <thead>
                <tr>
                    <th scope="col">Id</th>
                    <th scope="col">Nom PCA</th>
                    <th scope="col" class="min-w-100"></th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="feature in features" :key="feature.id">
                    <td>{{ feature.id }}</td>
                    <td>{{feature.pcaName}}</td>
                    <td>
                        <div class="float-end">
                            <FormButton color="link" title="Modifier la carte" @Click="editFeature(feature.id)" class="me-2 p-0 no-focus">
                                <Icon name="pencil" />
                            </FormButton>
                        </div>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</template>

<script lang="ts" setup>
import {PokemonComposables} from "@/vue/composables/pokemon/PokemonComposables";
import {isEmpty} from 'lodash';
import {ScrollToTop, SideButtons} from '@components/side';
import FormButton from "@components/form/FormButton.vue";
import Icon from "@components/Icon.vue";
import {usePageTitle} from "@/vue/composables/PageComposables";
import {useRouter} from "vue-router";

usePageTitle("Pokémon - Liste des particularités");

const router = useRouter();
const features = PokemonComposables.pokemonFeatureService.all;
const editFeature = (id: string) => {
    if (id) {
        router.push(`/cards/pokemon/features/${id}`);
    }
};

</script>
