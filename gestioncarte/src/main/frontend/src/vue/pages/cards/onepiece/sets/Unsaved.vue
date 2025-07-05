<template>
    <Loading :ready="isReady">
        <div class="container mt-4">
            <table class="table table-striped mt-4">
                <thead>
                    <tr>
                        <th scope="col" class="min-w-100">Nom de l'extension</th>
                        <th scope="col" class="min-w-100">Langues</th>
                        <th scope="col" class="min-w-100"></th>
                    </tr>
                </thead>
                <tbody>
                <tr v-for="(set, i) in unsavedSets" :key="i">
                    <td>
                        <OnePieceSetLabel :set="set" />
                    </td>
                    <td>
                        <Flag v-for="localization in set.translations" :key="localization" :lang="localization.localization" class="me-1" />
                    </td>
                    <td>
                        <div class="float-end">
                            <RouterLink title="Ajouter" :to="getRoute(set.code, set.id)" rel="noopener" class="btn btn-link me-2 p-0 no-focus">
                                <Icon name="add-outline" />
                            </RouterLink>
                        </div>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </Loading>
</template>

<script lang="ts" setup>
import Flag from "@/localization/Flag.vue";
import Icon from "@components/Icon.vue";
import {RouteLocationRaw, RouterLink} from "vue-router";
import Loading from "@components/Loading.vue";
import {useOnePieceTitle} from "@components/cards/onepiece/logic";
import {OnePieceSetLabel, useUnsavedSets} from "@components/cards/onepiece/set";

useOnePieceTitle("Liste des nouvelles extensions");

const { state: unsavedSets, isReady } = useUnsavedSets();

const getRoute = (code?: string, id?: string): RouteLocationRaw => ({
    path: id ? `/cards/onepiece/sets/${id}` : '/cards/onepiece/sets/new',
    query: {
        'code': code || 'promo',
    }
});

</script>
