            cardImageService.saveCardImage(
                    new CardImageDTO(
                            cardId,                    // cardId
                            image.localization().getCode(),    // localization (String) - corrigé
                            imageDTO.id(),             // imageId - corrigé
                            imageDTO.path()            // fichier - corrigé
                    )
            );
