<template>
    <Loading :ready="sets.length">
        <SideButtons>
            <ScrollToTop />
        </SideButtons>
        <div v-if="!isEmpty(sets)" class="full-screen-container mt-4">
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th scope="col">Name</th>
                        <th scope="col">Langues</th>
                        <th scope="col" class="min-w-100" />
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="set in sets" :key="set.id">
                        <td>{{ set.translations.us.name }}</td>
                        <td>
                            <div class="d-flex flex-row">
                                <Flag v-for="l in Object.keys(set.translations)" :key="l" :lang="l" class="me-1" />
                            </div>
                        </td>
                        <td>
                            <div class="float-end">
                                <a title="Modifier" :href="`/cards/lorcana/sets/${set.id}`" target="_blank" rel="noopener" class="btn btn-link me-2 p-0 no-focus">
                                    <Icon name="pencil" />
                                </a>
                                <a title="Cartes" :href="`/cards/lorcana/sets/${set.id}/list`" target="_blank" rel="noopener" class="btn btn-link me-2 p-0 no-focus">
                                    <Icon name="list" />
                                </a>
                            </div>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div v-else class="d-flex justify-content-center mt-2">
            <h4>Aucune carte trouvé</h4>
        </div>
    </Loading>
</template>

<script lang="ts" setup>
import {isEmpty} from 'lodash';
import {ScrollToTop, SideButtons} from '@components/side';
import Loading from "@components/Loading.vue";
import {useLorcanaTitle} from "@components/cards/lorcana/logic";
import {lorcanaSetService} from "@components/cards/lorcana/set";
import {Flag} from "@/localization";
import Icon from "@components/Icon.vue";

useLorcanaTitle("Liste de extensions");

const sets = lorcanaSetService.all;

</script>
