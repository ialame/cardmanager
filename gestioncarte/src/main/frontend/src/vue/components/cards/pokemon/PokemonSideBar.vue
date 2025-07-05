<template>
    <ul class="ps-0 ms-0">
        <Collapse open tag="li" label="Cartes">
            <ul>
                <SideBarLink to="/cards/pokemon/extraction" label="Lancer une extraction" />
                <SideBarLink to="/cards/pokemon/extracted" label="Cartes en attente" />
                <SideBarLink to="/cards/pokemon/new" label="Ajout manuel" />
                <SideBarLink to="/cards/pokemon/merge" label="Fusion manuelle" />
                <SideBarLink to="/cards/pokemon/search" label="Liste" />
            </ul>
        </Collapse>
        <Collapse open tag="li" label="Extensions">
            <ul>
                <SideBarLink to="/cards/pokemon/sets/new?localization=west" label="Ajout US" />
                <SideBarLink to="/cards/pokemon/sets/new?localization=jp" label="Ajout Jap" />
                <SideBarLink to="/cards/pokemon/sets/search" label="Modifier" />
                <SideBarLink to="/cards/pokemon/sets/parent" label="Ajout d'extension mère" />
                <SideBarLink to="/cards/pokemon/sets" label="Liste" />
            </ul>
        </Collapse>
        <Collapse open tag="li" label="Series">
            <ul>
                <SideBarLink to="/cards/pokemon/series/" label="Liste" />
                <SideBarLink to="/cards/pokemon/series/new" label="Ajout" />
            </ul>
        </Collapse>
        <Collapse open tag="li" label="Images">
            <ul>
                <SideBarLink to="/cards/pokemon/images" label="Extraction" />
            </ul>
        </Collapse>
        <Collapse open tag="li" label="Particularités">
            <ul>
                <SideBarLink to="/cards/pokemon/features" label="Liste" />
                <SideBarLink to="/cards/pokemon/features/new" label="Ajout" />
            </ul>
        </Collapse>
        <Collapse open tag="li" label="Crochets">
            <ul>
                <SideBarLink to="/cards/pokemon/brackets" label="Liste" />
                <SideBarLink to="/cards/pokemon/brackets/new" label="Ajout" />
            </ul>
        </Collapse>
        <Collapse open tag="li" label="Promo">
            <ul>
                <Collapse open tag="li" label="Evenement">
                    <ul>
                        <SideBarLink to="/cards/pokemon/promos/events/new" label="Ajout" />
                        <SideBarLink to="/cards/pokemon/promos/events/extracted" label="Promos sans évènement" />
                        <Collapse open tag="li" label="Caracteristiques">
                            <ul>
                                <SideBarLink to="/cards/pokemon/promos/events/traits" label="Liste" />
                                <SideBarLink to="/cards/pokemon/promos/events/traits/new" label="Ajout" />
                            </ul>
                        </Collapse>
                    </ul>
                </Collapse>
                <Collapse open tag="li" label="Versions">
                    <ul>
                        <SideBarLink to="/cards/pokemon/promos/versions" label="Liste" />
                        <SideBarLink to="/cards/pokemon/promos/versions/new" label="Ajout" />
                    </ul>
                </Collapse>
            </ul>
        </Collapse>
    </ul>
</template>

<script lang="ts" setup>
import Collapse from '@components/collapse/Collapse.vue';
import SideBarLink from "@components/sidebar/SideBarLink.vue";</script>
